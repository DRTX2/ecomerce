import { notFound } from "next/navigation";
import { AddToCartButton } from "../../components/cart";

type Product = { id:number; name:string; description:string; price:number; slug:string; status:string; stockQuantity:number; images:string[] | null };
const apiUrl = process.env.COMMERCE_API_URL ?? "http://localhost:8080/api/v1";
const price = new Intl.NumberFormat("es-CL", { style:"currency", currency:"CLP", maximumFractionDigits:0 });

export default async function ProductPage({ params }: { params: Promise<{ slug:string }> }) {
  const { slug } = await params;
  const response = await fetch(`${apiUrl}/products`, { next:{ revalidate:60 } });
  if (!response.ok) notFound();
  const product = ((await response.json()) as Product[]).find((item) => item.slug === slug && item.status === "ACTIVE");
  if (!product) notFound();
  return <main className="detail"><a href="/">← Volver al catalogo</a><div className="detail-grid"><div className="product-art">{product.images?.[0] ? <img src={product.images[0]} alt="" /> : <span>01</span>}</div><section><p className="eyebrow">Seleccion Norte</p><h1>{product.name}</h1><strong>{price.format(product.price)}</strong><p>{product.description}</p>{product.stockQuantity > 0 ? <AddToCartButton item={{ productId:product.id, name:product.name, price:product.price }} /> : <p>Agotado</p>}</section></div></main>;
}
