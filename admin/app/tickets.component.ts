import {Component, OnInit} from "@angular/core";
import {Ticket} from "./ticket";
import {TicketDetailComponent} from "./ticket-detail.component";
import {TicketService} from "./ticket.service";
import {Router} from "@angular/router-deprecated";

@Component({
    selector: 'my-tickets',
    templateUrl: 'app/templates/tickets.component.html',
    directives: [TicketDetailComponent],
})

export class TicketsComponent implements OnInit {
    tickets:Ticket[];
    ticket:Ticket;

    constructor(private router:Router,
                private ticketService:TicketService) {
    }

    ngOnInit() {
        this.getTickets();
    }

    onSelect(ticket:Ticket) {
        let link = ['TicketDetail', {id: ticket.id}];
        this.router.navigate(link);
    }

    getTickets() {
        this.ticketService.getTickets().then(tickets =>
            this.tickets = tickets);
    }
}
