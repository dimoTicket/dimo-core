import {Injectable} from "@angular/core";
import {TICKETS} from "./mock-tickets";
import {Ticket} from "./ticket";

@Injectable()
export class TicketService {

    getTickets() {
        // return Promise.resolve(TICKETS);
        return new Promise<Ticket[]>(resolve => setTimeout(()=>resolve(TICKETS), 200));
    }

    getTicket(id:number) {
        return this.getTickets()
            .then(tickets => tickets.filter(ticket => ticket.id === id)[0]);
    }
}