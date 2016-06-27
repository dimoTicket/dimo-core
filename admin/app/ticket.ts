export class Ticket {
    id:number;
    message:string;
    status:TicketStatus; //Should be kept in sync with the TickeStatus enum in the backend
    datetime:Date;

    constructor(id:number, message:string, status:string, datetime:string) {
        this.id = id;
        this.message = message;
        this.status = this.getStatusFromString(status);
        this.datetime = new Date(datetime);
    }

    static fromJSONArray(array:Array<Object>):Ticket[] {
        return array.map(obj => new Ticket(obj['id'], obj['message'], obj['status'], obj['datetime']));
    }

    static fromJSON(ticket:Object):Ticket {
        return new Ticket(ticket['id'], ticket['message'], ticket['status'], ticket['datetime']);
    }

    private getStatusFromString(status:string):TicketStatus {
        console.info("Parsing json status " + status + " to enum");
        let ticketStatus;
        if (status == "NEW") {
            ticketStatus = TicketStatus.NEW;
        } else if (status == "REJECTED") {
            ticketStatus = TicketStatus.REJECTED;
        } else if (status == "ASSIGNED") {
            ticketStatus = TicketStatus.ASSIGNED;
        } else if (status == "IN_PROGRESS") {
            ticketStatus = TicketStatus.IN_PROGRESS;
        } else if (status == "ABORTED") {
            ticketStatus = TicketStatus.ABORTED;
        } else if (status == "DONE") {
            ticketStatus = TicketStatus.DONE;
        } else {
            console.error("Cannot parse ticket status " + status + " from json");
            throw new TypeError("Json status " + status + " doesn't match any enum candidates");
        }
        console.info("Json status parsed to enum : " + ticketStatus);
        return ticketStatus;
    }

}

enum TicketStatus{
    NEW = <any>"New",
    REJECTED = <any>"Rejected",
    ASSIGNED = <any>"Assigned",
    IN_PROGRESS = <any>"In progress",
    ABORTED = <any>"Aborted",
    DONE = <any>"Done"
}