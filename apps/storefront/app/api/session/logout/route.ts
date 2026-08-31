import { cookies } from "next/headers";
import { NextResponse } from "next/server";

const apiUrl = process.env.COMMERCE_API_URL ?? "http://localhost:8080/api/v1";

export async function POST() {
  const store = await cookies();
  const accessToken = store.get("commerce_access")?.value;
  if (accessToken) await fetch(`${apiUrl}/auth/logout`, { method: "POST", headers: { Authorization: `Bearer ${accessToken}` } });
  store.delete("commerce_access");
  store.delete("commerce_refresh");
  return NextResponse.json({ loggedOut: true });
}
