import {Component, OnInit} from "@angular/core";
import {Ticket} from "./ticket";
import {TicketDetailComponent} from "./ticket-detail.component";
import {TicketService} from "./ticket.service";

@Component({
    selector: 'my-app',
    template: '<h1>{{title}}</h1>' +
    '<h2>All tickets</h2>' +
    '<ul class="tickets">' +
    '<li *ngFor="let ticket of tickets" (click)="onSelect(ticket)">' +
    '<span>id : {{ticket.id}}, message: {{ticket.message}}</span>' +
    '</li>' +
    '</ul>' +
    '<ticket-detail [ticket]="selectedTicket"></ticket-detail>',
    directives: [TicketDetailComponent],
    providers: [TicketService]
})

export class AppComponent implements OnInit {
    ngOnInit() {
        this.getTickets();
    }

    constructor(private ticketService:TicketService) {
    }

    title = 'Dimo Admin Panel';
    tickets:Ticket[];
    selectedTicket:Ticket;

    onSelect(ticket:Ticket) {
        this.selectedTicket = ticket;
    }

    getTickets() {
        this.ticketService.getTickets().then(tickets =>
            this.tickets = tickets);
    }
}
