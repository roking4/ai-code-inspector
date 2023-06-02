import './HeaderComponent.css'
import glass from '../../Images/glass.webp'
import { Outlet } from "react-router-dom";

function HeaderComponent() {
    return (
        <>
            <header className={"header"}>
                <h1 className={'header-text'}>AI Code Inspector</h1>
                <img className={"header-img"} src={glass} alt="glass"/>
            </header>
            <Outlet />
        </>
    );
}

export default HeaderComponent;
