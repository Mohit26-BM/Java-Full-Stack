import{useState,type FormEvent}from"react";
import{api,err}from"./api";
import type{Tx}from"./types";

type Kind="deposit"|"withdraw"|"transfer";

const inr=(n:number)=>n.toLocaleString("en-IN",{style:"currency",currency:"INR"});

/** A new key for every submit. If the same request is retried with its key, the server returns the first result instead of moving the money twice. */
const newKey=()=>typeof crypto!=="undefined"&&"randomUUID" in crypto?crypto.randomUUID():`${Date.now()}-${Math.random()}`;

export default function Transactions({role}:{role:string}){
 const[kind,setKind]=useState<Kind>(role==="CUSTOMER"?"withdraw":"deposit");
 const[account,setAccount]=useState("");
 const[toAccount,setToAccount]=useState("");
 const[amount,setAmount]=useState("");
 const[description,setDescription]=useState("");
 const[error,setError]=useState("");
 const[notice,setNotice]=useState("");
 const[statementOf,setStatementOf]=useState("");
 const[rows,setRows]=useState<Tx[]>([]);

 async function loadStatement(accountno:string){
  setError("");
  try{
   setRows((await api.get<Tx[]>(`/api/v1/accounts/${accountno}/transactions?limit=20`)).data);
   setStatementOf(accountno);
  }catch(e){setRows([]);setStatementOf("");setError(err(e));}
 }

 async function submit(e:FormEvent){
  e.preventDefault();setError("");setNotice("");
  const headers={"Idempotency-Key":newKey()};
  const desc=description.trim()||undefined;
  try{
   if(kind==="transfer")await api.post("/api/v1/accounts/transfer",{fromAccount:Number(account),toAccount:Number(toAccount),amount:Number(amount),description:desc},{headers});
   else await api.post(`/api/v1/accounts/${Number(account)}/${kind}`,{amount:Number(amount),description:desc},{headers});
   setNotice(kind==="deposit"?"Deposit completed.":kind==="withdraw"?"Withdrawal completed.":"Transfer completed.");
   setAmount("");setDescription("");
   await loadStatement(account);
  }catch(ex){setError(err(ex));}
 }

 return <>
  <div className="mb-4"><small className="eyebrow">MONEY MOVEMENT</small><h1>Transactions</h1><p className="text-secondary">Deposit, withdraw or transfer, and review an account statement.</p></div>
  {error&&<div className="alert alert-danger">{error}</div>}
  {notice&&<div className="alert alert-success">{notice}</div>}
  <div className="row g-4">
   <div className="col-12 col-xl-4"><form className="card panel" onSubmit={submit}>
    <h5>New transaction</h5>
    <label className="form-label">Type</label>
    <select className="form-select mb-3" value={kind} onChange={e=>setKind(e.target.value as Kind)}>{role!=="CUSTOMER"&&<option value="deposit">Deposit</option>}<option value="withdraw">Withdraw</option><option value="transfer">Transfer</option></select>
    <label className="form-label">{kind==="transfer"?"From account":"Account number"}</label>
    <input type="number" min="1" className="form-control mb-3" value={account} onChange={e=>setAccount(e.target.value)} required/>
    {kind==="transfer"&&<><label className="form-label">To account</label><input type="number" min="1" className="form-control mb-3" value={toAccount} onChange={e=>setToAccount(e.target.value)} required/></>}
    <label className="form-label">Amount</label>
    <input type="number" min="0.01" step=".01" className="form-control mb-3" value={amount} onChange={e=>setAmount(e.target.value)} required/>
    <label className="form-label">Note (optional)</label>
    <input className="form-control mb-4" maxLength={200} value={description} onChange={e=>setDescription(e.target.value)}/>
    <button className="btn btn-primary">Submit</button>
   </form></div>
   <div className="col-12 col-xl-8"><section className="card panel">
    <div className="d-flex justify-content-between mb-3 gap-2">
     <h5>Statement{statementOf&&` for account ${statementOf}`}</h5>
     <form className="d-flex gap-2" onSubmit={e=>{e.preventDefault();if(account)void loadStatement(account);}}>
      <button className="btn btn-outline-primary btn-sm" disabled={!account}>Show account {account||"…"}</button>
     </form>
    </div>
    <div className="table-responsive"><table className="table table-hover align-middle"><thead><tr><th>When</th><th>Type</th><th>Amount</th><th>Balance after</th><th>Other account</th><th>Note</th><th>By</th></tr></thead>
     <tbody>{rows.map(t=><tr key={t.id}><td>{t.createdAt.replace("T"," ")}</td><td>{t.type.replace("_"," ")}</td><td>{inr(t.amount)}</td><td>{inr(t.balanceAfter)}</td><td>{t.relatedAccountno??""}</td><td>{t.description??""}</td><td>{t.performedBy}</td></tr>)}</tbody></table></div>
    {rows.length===0&&<p className="text-secondary text-center py-4">Enter an account number and press the button to see its latest 20 lines.</p>}
   </section></div>
  </div>
 </>;
}
