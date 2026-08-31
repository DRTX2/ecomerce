"use client";

import { useEffect, useState } from "react";

type Order = { id:number; total:number; orderState:string; createdAt:string };
const price = new Intl.NumberFormat("es-CL", { style:"currency", currency:"CLP", maximumFractionDigits:0 });

export default function AccountPage() {
  const [orders, setOrders] = useState<Order[] | null>(null);
  const [message, setMessage] = useState("");
  useEffect(() => { fetch("/api/account/orders").then(async (response) => response.ok ? setOrders(await response.json()) : setMessage("Inicia sesion para ver tus pedidos.")).catch(() => setMessage("No fue posible cargar tus pedidos.")); }, []);
  const logout = async () => { await fetch("/api/session/logout", { method:"POST" }); setOrders(null); setMessage("Sesion cerrada."); };
  return <main className="account"><a href="/">← Catalogo</a><p className="eyebrow">Mi cuenta</p><h1>Pedidos y sesion</h1><button type="button" onClick={logout}>Cerrar sesion</button>{message ? <p role="status">{message}</p> : null}{orders === null && !message ? <p>Cargando pedidos...</p> : null}{orders?.length === 0 ? <p>Aun no tienes pedidos confirmados.</p> : null}<div className="orders">{orders?.map((order) => <article key={order.id}><strong>Pedido #{order.id}</strong><span>{order.orderState}</span><span>{price.format(order.total)}</span><time dateTime={order.createdAt}>{new Date(order.createdAt).toLocaleDateString("es-CL")}</time></article>)}</div></main>;
}
