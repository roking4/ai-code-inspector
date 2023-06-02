import Alert from '@mui/material/Alert';
import AlertTitle from '@mui/material/AlertTitle';
import ContentCopyIcon from '@mui/icons-material/ContentCopy';

function ResultsComponent(){
    return(
        <div>
            <p>Code Placeholder</p>
            <ContentCopyIcon />
            <Alert severity="error">
                <AlertTitle>Error</AlertTitle>
                <strong>1 test failed!</strong>
            </Alert>
            <Alert severity="success">
                <AlertTitle>Success</AlertTitle>
                <strong>All 10 scenarios pass!</strong>
            </Alert>
        </div>
    );
}

export default ResultsComponent;
