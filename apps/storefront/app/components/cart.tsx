"use client";

import { createContext, useContext, useEffect, useState, type FormEvent, type ReactNode } from "react";

type Item = { productId: number; name: string; price: number; quantity: number };
type CartContextValue = {
  items: Item[];
  isOpen: boolean;
  add: (item: Omit<Item, "quantity">) => void;
  changeQuantity: (productId: number, quantity: number) => void;
  remove: (productId: number) => void;
  clear: () => void;
  open: () => void;
  close: () => void;
};
const CartContext = createContext<CartContextValue | null>(null);

export function CartProvider({ children }: { children: ReactNode }) {
  const [items, setItems] = useState<Item[]>([]);
  const [isOpen, setIsOpen] = useState(false);
  const [hydrated, setHydrated] = useState(false);
  useEffect(() => {
    try {
      const saved = JSON.parse(localStorage.getItem("norte-cart") ?? "[]");
      if (Array.isArray(saved)) setItems(saved as Item[]);
    } catch {
      localStorage.removeItem("norte-cart");
    }
    setHydrated(true);
  }, []);
  useEffect(() => {
    if (hydrated) localStorage.setItem("norte-cart", JSON.stringify(items));
  }, [hydrated, items]);
  const add = (item: Omit<Item, "quantity">) => setItems((current) => {
    const existing = current.find((entry) => entry.productId === item.productId);
    return existing ? current.map((entry) => entry.productId === item.productId ? { ...entry, quantity: entry.quantity + 1 } : entry) : [...current, { ...item, quantity: 1 }];
  });
  const changeQuantity = (productId: number, quantity: number) => setItems((current) => quantity < 1 ? current.filter((item) => item.productId !== productId) : current.map((item) => item.productId === productId ? { ...item, quantity } : item));
  const remove = (productId: number) => setItems((current) => current.filter((item) => item.productId !== productId));
  return <CartContext.Provider value={{ items, isOpen, add, changeQuantity, remove, clear: () => setItems([]), open: () => setIsOpen(true), close: () => setIsOpen(false) }}>{children}</CartContext.Provider>;
}

export function AddToCartButton({ item }: { item: Omit<Item, "quantity"> }) {
  const cart = useContext(CartContext);
  return <button type="button" className="cta" onClick={() => { cart?.add(item); cart?.open(); }}>Agregar a la bolsa <span>+</span></button>;
}

export function CartButton() {
  const cart = useContext(CartContext);
  if (!cart) return null;
  const quantity = cart.items.reduce((total, item) => total + item.quantity, 0);
  return <button className="bag" type="button" onClick={cart.open} aria-label={`Ver carrito, ${quantity} productos`}>Bolsa <span>{quantity}</span></button>;
}

export function CartPanel() {
  const cart = useContext(CartContext);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const [showLogin, setShowLogin] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  if (!cart) return null;
  const total = cart.items.reduce((sum, item) => sum + item.price * item.quantity, 0);
  const login = async (event: FormEvent) => {
    event.preventDefault();
    setSubmitting(true);
    const response = await fetch("/api/session/login", { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ email, password }) });
    setSubmitting(false);
    setMessage(response.ok ? "Sesion iniciada. Ya puedes confirmar." : "No fue posible iniciar sesion.");
    if (response.ok) setShowLogin(false);
  };
  const checkout = async () => {
    setSubmitting(true);
    const response = await fetch("/api/checkout", { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ items: cart.items.map(({ productId, quantity }) => ({ productId, quantity })) }) });
    const payload = await response.json();
    setSubmitting(false);
    if (response.ok) {
      cart.clear();
      setMessage("Pedido confirmado correctamente.");
      return;
    }
    setMessage(payload.message ?? "No fue posible confirmar.");
    if (response.status === 401) setShowLogin(true);
  };
  if (!cart.isOpen) return null;
  return <div className="cart-overlay" role="presentation" onMouseDown={cart.close}><aside className="cart-panel" role="dialog" aria-modal="true" aria-label="Bolsa de compra" onMouseDown={(event) => event.stopPropagation()}><div className="cart-heading"><strong>Bolsa / {cart.items.reduce((sum, item) => sum + item.quantity, 0)}</strong><button type="button" className="cart-close" onClick={cart.close} aria-label="Cerrar bolsa">Cerrar</button></div>{cart.items.length ? <div className="cart-items">{cart.items.map((item) => <div className="cart-item" key={item.productId}><span>{item.name}</span><span>${(item.price * item.quantity).toFixed(2)}</span><div><button type="button" onClick={() => cart.changeQuantity(item.productId, item.quantity - 1)} aria-label={`Reducir ${item.name}`}>-</button><span>{item.quantity}</span><button type="button" onClick={() => cart.changeQuantity(item.productId, item.quantity + 1)} aria-label={`Aumentar ${item.name}`}>+</button><button type="button" onClick={() => cart.remove(item.productId)}>Quitar</button></div></div>)}</div> : <p>Tu bolsa esta vacia.</p>}<div className="cart-total"><span>Total</span><strong>${total.toFixed(2)}</strong></div>{showLogin ? <form onSubmit={login}><input aria-label="Correo" type="email" value={email} onChange={(event) => setEmail(event.target.value)} placeholder="Correo" required /><input aria-label="Contrasena" type="password" value={password} onChange={(event) => setPassword(event.target.value)} placeholder="Contrasena" required /><button type="submit" disabled={submitting}>Iniciar sesion</button></form> : <button type="button" className="cart-login" onClick={() => setShowLogin(true)}>Ya tengo cuenta</button>}<button type="button" disabled={!cart.items.length || submitting} onClick={checkout}>Confirmar pedido</button>{message ? <p role="status">{message}</p> : null}</aside></div>;
}
