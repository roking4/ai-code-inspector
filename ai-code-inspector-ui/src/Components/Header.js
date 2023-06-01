import './Header.css'
import glass from '../Images/glass.webp'

function Header() {
    return (
        <header className={"header"}>
            <h1 className={'header-text'}>AI Code Inspector</h1>
            <img className={"header-img"} src={glass} alt="glass"/>
        </header>
    );
}

export default Header;
