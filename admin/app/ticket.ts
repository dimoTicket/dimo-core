export class Ticket {
    id:number;
    message:string;
    status:string; //Should be kept in sync with the TickeStatus enum in the backend
    datetime:Date;


    constructor(id:number, message:string, status:string, datetime:string) {
        this.id = id;
        this.message = message;
        this.status = status;
        this.datetime = new Date(datetime);
    }

    static fromJSONArray(array:Array<Object>):Ticket[] {
        return array.map(obj => new Ticket(obj['id'], obj['message'], obj['status'], obj['datetime']));
    }
}