import './HomeComponent.css'
import TextareaAutosize from '@mui/base/TextareaAutosize';
import { useState } from "react";
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import { useNavigate } from "react-router-dom";

function HomeComponent(props) {

    const navigate = useNavigate();
    const [formPrompt, setFormPrompt] = useState("I would like a java function that ");

    const handlePromptChange = (e) => {
        setFormPrompt(e.target.value);
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        props.setPrompt(formPrompt);
        navigate('enter-tests');
    };

    return (
        <div>
            <form onSubmit={ handleSubmit }>
                <div className={"textArea-container"}>
                    <TextareaAutosize
                        required
                        className={'text-area'}
                        minRows={6}
                        placeholder={"Prompt to AI"}
                        value={ formPrompt }
                        onChange={ handlePromptChange }
                    />
                </div>
                <Button className={ "add-button" } onClick={ handleSubmit } disabled={ formPrompt === '' } variant="contained">Submit</Button>
            </form>
        </div>
    );
}

export default HomeComponent;
