import { cookies } from "next/headers";
import { NextResponse } from "next/server";

const apiUrl = process.env.COMMERCE_API_URL ?? "http://localhost:8080/api/v1";

export async function POST(request: Request) {
  const { items } = await request.json() as { items: Array<{ productId: number; quantity: number }> };
  if (!Array.isArray(items) || items.length === 0 || items.some((item) => !Number.isInteger(item.productId) || !Number.isInteger(item.quantity) || item.quantity < 1)) return NextResponse.json({ message: "Carrito invalido" }, { status: 400 });
  const store = await cookies();
  let accessToken = store.get("commerce_access")?.value;
  if (!accessToken) return NextResponse.json({ message: "Inicia sesion para confirmar tu pedido" }, { status: 401 });
  const createCart = (token: string) => fetch(`${apiUrl}/carts`, { method: "POST", headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` }, body: JSON.stringify({ items }) });
  let cartResponse = await createCart(accessToken);
  if (cartResponse.status === 401) {
    const refreshToken = store.get("commerce_refresh")?.value;
    if (!refreshToken) return NextResponse.json({ message: "Sesion expirada" }, { status: 401 });
    const refresh = await fetch(`${apiUrl}/auth/refresh`, { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ refreshToken }) });
    if (!refresh.ok) return NextResponse.json({ message: "Sesion expirada" }, { status: 401 });
    const refreshed = await refresh.json();
    const refreshedAccessToken = refreshed.tokens.accessToken as string;
    accessToken = refreshedAccessToken;
    store.set("commerce_access", refreshedAccessToken, { httpOnly: true, sameSite: "lax", secure: process.env.NODE_ENV === "production", path: "/", maxAge: Math.floor(refreshed.tokens.expiresInMs / 1000) });
    cartResponse = await createCart(refreshedAccessToken);
  }
  if (!cartResponse.ok) return NextResponse.json({ message: "No fue posible crear el carrito" }, { status: cartResponse.status });
  const cart = await cartResponse.json();
  const orderResponse = await fetch(`${apiUrl}/orders/from-cart/${cart.id}`, { method: "POST", headers: { "Content-Type": "application/json", Authorization: `Bearer ${accessToken}` } });
  if (!orderResponse.ok) return NextResponse.json({ message: "No fue posible confirmar el pedido" }, { status: orderResponse.status });
  return NextResponse.json(await orderResponse.json(), { status: 201 });
}
