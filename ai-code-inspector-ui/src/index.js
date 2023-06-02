import React, { useState } from 'react';
import ReactDOM from "react-dom/client";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import './Components/Header/HeaderComponent.css';
import reportWebVitals from './reportWebVitals';
import Layout from "./Layout";
import HomeComponent from "./Components/Home/HomeComponent";
import ResultsComponent from "./Components/Results/ResultsComponent";
import NotFoundComponent from "./Components/NotFound/NotFoundComponent";

export default function App() {
    const [prompt, setPrompt] = useState();
    const [scenarios, setScenarios] = useState();
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<Layout />}>
                    <Route index element={ <HomeComponent setPrompt={ setPrompt } setScenarios={ setScenarios }/> } />
                    <Route path={'results'} element={ <ResultsComponent prompt={ prompt } scenarios={ setScenarios }/> } />
                    <Route path={'*'} element={<NotFoundComponent />} />
                </Route>
            </Routes>
        </BrowserRouter>
    );
}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<App />);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
