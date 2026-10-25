import { Link, Routes, Route } from 'react-router-dom';
import './App.css'
import Demos from './Demos';
import Home from './Home';
import About from './About';
import Contact from './Contact';
import Login from './Login';
import Success from './Success';
import Failure from './Failure';
import ProductDetails from './ProductDetails';
// import Register from './Register'; // replaced by ProductValidate
import ProductValidate from './ProductValidate';
import PromiseRest from './PromiseRest';
import PromiseExample from './PromiseExample';
import HotelList from './HotelList';
import NotFound from './NotFound';

function App() {

  return (
    <div>
      <h1>React Routing Demo</h1>

      <nav>
        <Link to="/">Home</Link>{" | "}
        <Link to="/about">About</Link>{" | "}
        <Link to="/contact">Contact</Link>{" | "}
        <Link to="/login">Login</Link>{" | "}
        <Link to="/register">Register</Link>{" | "}
        <Link to="/product/101/Laptop">Product</Link>{" | "}
        <Link to="/promise-rest">Promise REST</Link>{" | "}
        <Link to="/promise-example">Promise Login</Link>{" | "}
        <Link to="/hotels">Hotels</Link>{" | "}
        <Link to="/demos">Demos</Link>
      </nav>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/about" element={<About />} />
        <Route path="/contact" element={<Contact />} />
        <Route path="/login" element={<Login />} />
        {/* <Route path="/register" element={<Register />} /> */}
        <Route path="/register" element={<ProductValidate />} />
        <Route path="/success" element={<Success />} />
        <Route path="/failure" element={<Failure />} />
        <Route path="/product/:id/:name" element={<ProductDetails />} />
        <Route path="/promise-rest" element={<PromiseRest />} />
        <Route path="/promise-example" element={<PromiseExample />} />
        <Route path="/hotels" element={<HotelList />} />
        <Route path="/demos" element={<Demos />} />
        <Route path="*" element={<NotFound />} />
      </Routes>
    </div>
  );

}

export default App
