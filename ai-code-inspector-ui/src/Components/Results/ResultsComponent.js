import Alert from '@mui/material/Alert';
import AlertTitle from '@mui/material/AlertTitle';
import ContentCopyIcon from '@mui/icons-material/ContentCopy';
import { useEffect, useState } from "react";
import CheckCircleRoundedIcon from '@mui/icons-material/CheckCircleRounded';
import ErrorRoundedIcon from '@mui/icons-material/ErrorRounded';
import { red } from '@mui/material/colors';
import Button from "@mui/material/Button";
import './ResultsComponent.css';
import AiCodeService from "../../Services/AiCodeService";
import { useNavigate } from "react-router-dom";

function ResultsComponent(props){

    const navigate = useNavigate();
    const [code, setCode] = useState();
    const [results, setResults] = useState({
        errors: 0,
        scenarios: [
                {
                    index: 1,
                    inputs: "1, 2, horse",
                    output: 4,
                    pass: true
                }
            ]
    });

    useEffect(() => {
        if(!props.prompt || !props.scenarios){
            navigate('/')
        }
        getAiCode()
    }, []);

    const getAiCode = () => {
        AiCodeService.getAiCode(props.prompt)
            .then((response) => {
                setCode(response.data.code);
            }).catch((error) => {
                console.log(error);
        });
    }

    const handleRegenerateCode = (e) => {
        e.preventDefault();
        getAiCode();
    }

    return(
        <div>
            <form>
                <p>{code}</p>
                <ContentCopyIcon />
                {
                    results.errors >= 1 ?
                        <Alert severity="error">
                            <AlertTitle>Error</AlertTitle>
                            <strong>{results.errors} test failed!</strong>
                        </Alert>
                    :
                        <Alert severity="success">
                            <AlertTitle>Success</AlertTitle>
                            <strong>All scenarios pass!</strong>
                        </Alert>
                }
                {
                    results.scenarios !== [] ?
                        results.scenarios.map((scenario) =>
                            <div>
                                <h2>
                                    Test Scenario {scenario.index}
                                    {scenario.pass === true ?
                                        <CheckCircleRoundedIcon color="success" /> :
                                        <ErrorRoundedIcon sx={{ color: red[500] }} />}
                                </h2>
                                <p>Inputs: [{scenario.inputs}]</p>
                                <p>Output: {scenario.output}</p>
                            </div>
                        )
                        : null
                }
                <Button onClick={ handleRegenerateCode } variant="contained">Regenerate Code</Button>
                <Button href={ '/' } className={ "new-prompt-button" } variant="contained">New Prompt</Button>
            </form>
        </div>
    );
}

export default ResultsComponent;
