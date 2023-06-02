import { Outlet } from "react-router-dom";
import HeaderComponent from "./Components/Header/HeaderComponent";
import FooterComponent from "./Components/Footer/FooterComponent";

function Layout() {
    return(
        <>
            <HeaderComponent />
            <Outlet />
            <FooterComponent />
        </>
    );
}

export default Layout;
