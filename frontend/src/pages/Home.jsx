import TopBar from '../components/layout/TopBar';
import Header from '../components/layout/Header';
import Footer from '../components/layout/Footer';
import TopNoticeBar from '../components/layout/TopNoticeBar';
import MenuHeader from '../components/layout/MenuHeader';

import HeroSection from '../components/home/HeroSection';
import HomeContent from "../components/home/HomeContent";


import '../styles/home.css';
import "../styles/TopBar.css";   // ✅ FIXED PATH
import "../styles/MenuHeader.css";   // ✅ FIXED PATH


const Home = () => {
  return (
    <>
      <TopBar />
      <Header />
      <TopNoticeBar />
      <MenuHeader />
      <HeroSection />
      <HomeContent />
      <Footer />
    </>
  );
};

export default Home;
