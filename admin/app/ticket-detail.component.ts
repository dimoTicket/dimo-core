import {Component, Input} from "@angular/core";
import {Ticket} from "./ticket";

@Component({
    selector: 'ticket-detail',
    template: '<div *ngIf="ticket">' +
    '<h2>Selected ticket id : {{ticket.id}}</h2>' +
    '<label>message: </label> <input [(ngModel)]="ticket.message" placeholder="message"/>' +
    '</div>'
})

export class TicketDetailComponent {
    @Input()
    ticket:Ticket;
}