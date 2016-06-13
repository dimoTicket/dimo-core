import {Injectable} from "@angular/core";
import {Headers, Http} from "@angular/http";
import {Ticket} from "./ticket";
import "rxjs/add/operator/toPromise";

@Injectable()
export class TicketService {

    private ticketsUrl = 'app/tickets';  // URL to web api

    constructor(private http:Http) {
    }

    getTickets():Promise<Ticket[]> {
        return this.http.get(this.ticketsUrl)
            .toPromise()
            .then(response => response.json().data)
            .catch(this.handleError);
    }

    getTicket(id:number) {
        return this.getTickets()
            .then(tickets => tickets.filter(ticket => ticket.id === id)[0]);
    }

    private handleError(error:any) {
        console.error('An error occurred', error);
        return Promise.reject(error.message || error);
    }
}