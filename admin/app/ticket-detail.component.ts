import {Component, OnInit} from "@angular/core";
import {ActivatedRoute, ROUTER_DIRECTIVES, Router} from "@angular/router";
import {TicketService} from "./ticket.service";
import {Ticket, TicketStatus} from "./ticket";

@Component({
    selector: 'my-ticket-detail',
    templateUrl: 'app/templates/ticket-detail.component.html',
    directives: [ROUTER_DIRECTIVES]
})

export class TicketDetailComponent implements OnInit {

    private ticket:Ticket;
    statuses = ["s", "b"];

    constructor(private ticketService:TicketService, private route:ActivatedRoute, private router:Router) {
    }

    ngOnInit() {
        this.route.params.subscribe(params => {
            let id = +params['id'];
            this.ticketService.getTicket(id).subscribe(ticket => this.ticket = ticket);
        });
    }

    goBack() {
        this.goToTickets();
    }

    goToTickets() {
        this.router.navigate(['/tickets']);
    }
}