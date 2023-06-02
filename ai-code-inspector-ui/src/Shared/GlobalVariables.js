const AI_CODE_BASE_REST_API_URL = "http://localhost:8080/api/v1";

class GlobalVariables{

    getBaseApi() {
        return AI_CODE_BASE_REST_API_URL;
    }

}

export default new GlobalVariables()
