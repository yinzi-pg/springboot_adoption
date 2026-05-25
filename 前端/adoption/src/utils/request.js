import axios from "axios";
const baseURL = "http://localhost:8080/springboot_adoption/";
const instance = axios.create({
    baseURL,
    withCredentials: true
});

instance.interceptors.response.use(
    (result) => {
        return result.data;
    },
    (error) => {
        const res = error.response;
        if (res) {
            switch (res.status) {
                case 401:
                    window.location.href = "/login";
                    break;
                case 403:
                    alert("无权限访问");
                    break;
                case 500:
                    alert("服务器异常！");
                    break;
            }
        } else {
            alert("网络或服务异常！");
        }
        return Promise.reject(error);
    }
);

export default instance;