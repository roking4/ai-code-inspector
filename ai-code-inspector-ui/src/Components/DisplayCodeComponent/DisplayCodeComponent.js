import Box from "@mui/material/Box";
import CircularProgress from "@mui/material/CircularProgress";
import ContentCopyIcon from "@mui/icons-material/ContentCopy";

function DisplayCodeComponent(props) {

    const handleCopyToClipBoard = () => {
        navigator.clipboard.writeText(props.code);
    };

    return (
        <div>
            {
                props.code === undefined ?
                    <Box sx={{display: 'flex', justifyContent: 'center', alignContent: 'center'}}>
                        <CircularProgress/>
                    </Box>
                    :
                    <div>
                        <p>{ props.code }</p>
                        <ContentCopyIcon onClick={ handleCopyToClipBoard }/>
                    </div>
            }
        </div>
    );
}

export default DisplayCodeComponent;
