export interface Customer{customerId:number;name:string;email:string;dateOfBirth:string;gender:string;city:string}
export interface Account{accountno:number;customerId:number;accountType:string;openingDate:string;balance:number;status:string}
export interface Tx{id:number;reference:string;accountno:number;type:string;amount:number;balanceAfter:number;relatedAccountno:number|null;description:string|null;performedBy:string;createdAt:string}
