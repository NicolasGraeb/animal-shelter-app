import './Hero.css';
import {Link} from "react-router-dom";

function Hero() {
  return (
    <section id="hero" className="hero-section">
      <h1 className="hero-title">Adopt A Furry Friend</h1>
      <p className="hero-subtitle">Find your perfect companion today.</p>
         <Link to="/adopt">
      <button className="hero-button">Get Started</button>
         </Link>
    </section>
  );
}

export default Hero;