import { cookies } from "next/headers";
import { NextResponse } from "next/server";

const apiUrl = process.env.COMMERCE_API_URL ?? "http://localhost:8080/api/v1";
const options = { httpOnly: true, sameSite: "lax" as const, secure: process.env.NODE_ENV === "production", path: "/" };

export async function POST() {
  const store = await cookies();
  const refreshToken = store.get("commerce_refresh")?.value;
  if (!refreshToken) return NextResponse.json({ message: "Sesion expirada" }, { status: 401 });
  const response = await fetch(`${apiUrl}/auth/refresh`, { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ refreshToken }) });
  if (!response.ok) {
    store.delete("commerce_access");
    store.delete("commerce_refresh");
    return NextResponse.json({ message: "Sesion expirada" }, { status: 401 });
  }
  const result = await response.json();
  store.set("commerce_access", result.tokens.accessToken, { ...options, maxAge: Math.floor(result.tokens.expiresInMs / 1000) });
  return NextResponse.json({ refreshed: true });
}
