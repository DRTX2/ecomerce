import type { Metadata } from "next";
import type { ReactNode } from "react";
import "./styles.css";
import { CartPanel, CartProvider } from "./components/cart";

export const metadata: Metadata = {
  title: "Norte | Objetos bien elegidos",
  description: "Storefront de Commerce Platform"
};

export default function RootLayout({ children }: Readonly<{ children: ReactNode }>) {
  return <html lang="es"><body><CartProvider>{children}<CartPanel /></CartProvider></body></html>;
}
