import {Component, OnInit} from "@angular/core";
import {RouteParams} from "@angular/router-deprecated";
import {TicketService} from "./ticket.service";
import {Ticket} from "./ticket";

@Component({
    selector: 'my-ticket-detail',
    templateUrl: 'app/templates/ticket-detail.component.html'
})

export class TicketDetailComponent implements OnInit {

    ticket:Ticket;

    constructor(private ticketService:TicketService,
                private routeParams:RouteParams) {
    }

    ngOnInit() {
        let id = +this.routeParams.get('id');
        this.ticketService.getTicket(id).then(ticket => this.ticket = ticket);
    }

    goBack() {
        window.history.back();
    }

}