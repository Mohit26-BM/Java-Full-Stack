import axios from "axios";
export const authUrl=import.meta.env.VITE_AUTH_BASE_URL||"http://localhost:9898";
export const api=axios.create({baseURL:import.meta.env.VITE_GATEWAY_BASE_URL||"http://localhost:8765",timeout:15000,headers:{"Content-Type":"application/json"}});
let token:string|null=null;
export function setToken(v:string|null){token=v;}
api.interceptors.request.use(c=>{if(token)c.headers.set("Authorization",`Bearer ${token}`);else c.headers.delete("Authorization");return c;});
api.interceptors.response.use(r=>r,e=>{console.error("API error",e.response?.status,e.response?.data,e.message);if(e.response?.status===401)window.dispatchEvent(new Event("auth-expired"));return Promise.reject(e);});
/** The message to show for a failed request: the service's own message when it sent one, otherwise a friendly default. */
export function err(e:unknown){
 if(axios.isAxiosError(e)){
  if(!e.response)return"Network error. Verify services and Gateway CORS configuration.";
  const{status,data}=e.response;
  if(status===401)return"Session expired or invalid. Please sign in again.";
  if(typeof data==="string"&&data.trim())return data;
  if(data&&typeof data==="object"&&"message" in data&&typeof data.message==="string"&&data.message)return data.message;
  if(status===503)return"A backend service is unavailable. Make sure the Eureka, Customer and Account services are running.";
  if(status===404)return"API route not found.";
  return`Request failed (${status}).`;
 }
 return e instanceof Error?e.message:"Unexpected error";
}

/** The role claim inside a token (display only: the services check the token themselves, this just hides buttons that would fail). */
export function roleOf(token:string):string{try{const p=token.split(".")[1].replace(/-/g,"+").replace(/_/g,"/");return JSON.parse(atob(p)).role??"";}catch{return"";}}
