import { cookies } from "next/headers";
import { NextResponse } from "next/server";

const apiUrl = process.env.COMMERCE_API_URL ?? "http://localhost:8080/api/v1";
const cookieOptions = { httpOnly: true, sameSite: "lax" as const, secure: process.env.NODE_ENV === "production", path: "/" };

export async function POST(request: Request) {
  const body = await request.json();
  const response = await fetch(`${apiUrl}/auth/login`, { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify(body) });
  if (!response.ok) return NextResponse.json({ message: "Credenciales invalidas" }, { status: response.status });
  const result = await response.json();
  const store = await cookies();
  store.set("commerce_access", result.tokens.accessToken, { ...cookieOptions, maxAge: Math.floor(result.tokens.expiresInMs / 1000) });
  store.set("commerce_refresh", result.tokens.refreshToken, { ...cookieOptions, maxAge: 60 * 60 * 24 * 7 });
  return NextResponse.json({ user: result.user });
}
