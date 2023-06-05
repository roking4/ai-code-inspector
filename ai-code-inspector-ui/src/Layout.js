import { Outlet } from "react-router-dom";
import './index.css'
import HeaderComponent from "./Components/Header/HeaderComponent";
import FooterComponent from "./Components/Footer/FooterComponent";

function Layout() {
    return(
        <>
            <HeaderComponent />
            {
                <div className={ "outlet-container" } >
                    <Outlet />
                </div>
            }
            <FooterComponent />
        </>
    );
}

export default Layout;
