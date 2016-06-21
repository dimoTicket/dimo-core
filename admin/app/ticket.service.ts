import {Injectable} from "@angular/core";
import {Http} from "@angular/http";
import {Ticket} from "./ticket";
import {Observable} from "rxjs/Observable";


@Injectable()
export class TicketService {

    private ticketsUrl = 'app/tickets';  // URL to web api

    constructor(private http:Http) {
    }

    getTickets():Observable<Ticket[]> {
        return this.http.get(this.ticketsUrl)
            .map(response => Ticket.fromJSONArray(response.json().data))
            .catch(this.handleError);
    }

    getTicket(id:number):Observable<Ticket> {
        return this.getTickets().map(tickets => tickets.filter(ticket => ticket.id === id)[0]);
    }

    private handleError(error:any) {
        console.error('An error occurred', error);
        return Observable.throw(error.message || error);
    }
}