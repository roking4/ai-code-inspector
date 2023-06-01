import './HomeComponent.css'
import TextareaAutosize from '@mui/base/TextareaAutosize';

function HomeComponent() {
    return (
        <div className={"text-area-container"}>
            <TextareaAutosize
                className={'text-area'}
                minRows={6}
                placeholder={"Prompt to AI"}
            />
        </div>
    );
}

export default HomeComponent;
