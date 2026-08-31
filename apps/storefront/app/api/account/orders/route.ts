import { cookies } from "next/headers";
import { NextResponse } from "next/server";

const apiUrl = process.env.COMMERCE_API_URL ?? "http://localhost:8080/api/v1";

export async function GET() {
  const token = (await cookies()).get("commerce_access")?.value;
  if (!token) return NextResponse.json({ message: "Inicia sesion" }, { status: 401 });
  const response = await fetch(`${apiUrl}/orders/mine`, { headers: { Authorization: `Bearer ${token}` }, cache: "no-store" });
  if (!response.ok) return NextResponse.json({ message: "No fue posible obtener pedidos" }, { status: response.status });
  return NextResponse.json(await response.json());
}
