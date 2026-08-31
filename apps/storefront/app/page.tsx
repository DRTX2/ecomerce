import { Catalog } from "./components/catalog";
import { CartButton } from "./components/cart";

type Category = { id: number; name: string };
type Product = {
  id: number;
  name: string;
  description: string;
  price: number;
  category: Category | null;
  images: string[] | null;
  stockQuantity: number;
  status: "DRAFT" | "ACTIVE" | "ARCHIVED" | "OUT_OF_STOCK";
  slug: string;
};

const apiUrl = process.env.COMMERCE_API_URL ?? "http://localhost:8080/api/v1";

async function getProducts(): Promise<Product[] | null> {
  try {
    const response = await fetch(`${apiUrl}/products/search?size=60`, { next: { revalidate: 60 } });
    if (!response.ok) return null;
    const result = await response.json() as { content?: Product[] } | Product[];
    return Array.isArray(result) ? result : result.content ?? [];
  } catch {
    return null;
  }
}

export default async function Home() {
  const products = await getProducts();
  const visibleProducts = products?.filter((product) => product.status === "ACTIVE") ?? [];

  return <main>
    <nav className="nav" aria-label="Navegacion principal">
      <a className="wordmark" href="#inicio">NORTE<span>.</span></a>
      <div className="nav-links"><a href="#catalogo">Catalogo</a><a href="#criterio">Nuestro criterio</a></div>
      <CartButton />
    </nav>

    <section className="hero" id="inicio">
      <p className="eyebrow">Temporada 01 / seleccion esencial</p>
      <h1>Menos ruido.<br /><i>Mejores objetos.</i></h1>
      <p className="hero-copy">Una vitrina editorial para productos que resuelven bien la vida diaria. Sin descuentos ficticios, sin urgencia fabricada.</p>
      <a className="cta" href="#catalogo">Explorar seleccion <span>↓</span></a>
      <div className="hero-index">01 <span /> 03</div>
    </section>

    <Catalog products={visibleProducts} unavailable={products === null} />

    <section className="criterion" id="criterio"><p className="eyebrow">El criterio Norte</p><h2>No vendemos de todo.<br />Elegimos <i>lo necesario.</i></h2><p>Cada producto pasa por utilidad, durabilidad y claridad. La plataforma muestra su disponibilidad real porque la confianza tambien es una caracteristica.</p></section>
    <footer><span>Norte / Commerce Platform</span><span>Disenado para funcionar con datos reales</span></footer>
  </main>;
}
