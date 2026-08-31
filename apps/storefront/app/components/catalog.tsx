"use client";

import { useDeferredValue, useState } from "react";

type Product = { id:number; name:string; price:number; category:{ name:string } | null; images:string[] | null; stockQuantity:number; status:string; slug:string };
const price = new Intl.NumberFormat("es-CL", { style:"currency", currency:"CLP", maximumFractionDigits:0 });

export function Catalog({ products, unavailable }: { products: Product[]; unavailable: boolean }) {
  const [query, setQuery] = useState("");
  const [category, setCategory] = useState("all");
  const deferredQuery = useDeferredValue(query.trim().toLowerCase());
  const categories = [...new Set(products.map((product) => product.category?.name).filter(Boolean))] as string[];
  const visible = products.filter((product) => product.status === "ACTIVE" && (category === "all" || product.category?.name === category) && product.name.toLowerCase().includes(deferredQuery));
  return <section className="catalog" id="catalogo"><div className="section-heading"><p className="eyebrow">Disponible ahora</p><h2>La seleccion</h2><p>{visible.length} piezas</p></div>{unavailable ? <div className="connection-note"><strong>El catalogo no esta disponible.</strong><span>Inicia el backend o define `COMMERCE_API_URL`.</span></div> : null}<div className="catalog-controls"><input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Buscar productos" aria-label="Buscar productos" /><select value={category} onChange={(event) => setCategory(event.target.value)} aria-label="Filtrar por categoria"><option value="all">Todas las categorias</option>{categories.map((name) => <option key={name}>{name}</option>)}</select></div>{!unavailable && visible.length === 0 ? <div className="connection-note"><strong>Sin resultados.</strong><span>Prueba otra busqueda o categoria.</span></div> : null}<div className="product-grid">{visible.map((product,index) => <article className={`product product-${index % 5}`} key={product.id}><a href={`/products/${product.slug}`}><div className="product-art">{product.images?.[0] ? <img src={product.images[0]} alt="" /> : <span>{String(index+1).padStart(2,"0")}</span>}</div><div className="product-info"><p>{product.category?.name ?? "Seleccion Norte"}</p><h3>{product.name}</h3><div><strong>{price.format(product.price)}</strong><span>{product.stockQuantity > 0 ? `${product.stockQuantity} disponibles` : "Agotado"}</span></div></div></a></article>)}</div></section>;
}
