import {Component, OnInit} from "@angular/core";
import {RouteParams} from "@angular/router-deprecated";
import {TicketService} from "./ticket.service";
import {Ticket} from "./ticket";

@Component({
    selector: 'my-ticket-detail',
    templateUrl: 'app/templates/ticket-detail.component.html'
})

export class TicketDetailComponent implements OnInit {

    private ticket:Ticket;

    constructor(private ticketService:TicketService,
                private routeParams:RouteParams) {
    }

    ngOnInit() {
        let id = +this.routeParams.get('id');
        this.ticketService.getTicket(id).subscribe(ticket => {
            this.ticket = ticket;
            console.log(this.ticket);
        });
    }

    goBack() {
        window.history.back();
    }

}