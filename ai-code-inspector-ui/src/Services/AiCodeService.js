import axios from 'axios';
import GlobalVariables from '../Shared/GlobalVariables';

const BASE_REST_API_URL = GlobalVariables.getBaseApi() + "/ai-code";

class AiCodeService{
    getAiCode(prompt) {
        return axios.post(BASE_REST_API_URL, {
                prompt: prompt
            }
        );
    }
    getTestResults(code, inputs, output) {
        return axios.post(BASE_REST_API_URL + "/test", {
                code: code,
                inputs: inputs,
                output: output
            }
        );
    }
}

export default new AiCodeService();
