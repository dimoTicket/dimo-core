import {Component, OnInit} from "@angular/core";
import {ActivatedRoute, ROUTER_DIRECTIVES, Router} from "@angular/router";
import {TicketService} from "./ticket.service";
import {Ticket} from "./ticket";
import {SELECT_DIRECTIVES} from "ng2-select";

@Component({
    selector: 'my-ticket-detail',
    templateUrl: 'app/templates/ticket-detail.component.html',
    directives: [
        ROUTER_DIRECTIVES,
        SELECT_DIRECTIVES]
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

    //ng2-select stuff
    public items:Array<string> = ['Amsterdam', 'Antwerp', 'Athens', 'Barcelona',
        'Berlin', 'Birmingham', 'Bradford', 'Bremen', 'Brussels', 'Bucharest',
        'Budapest', 'Cologne', 'Copenhagen', 'Dortmund', 'Dresden', 'Dublin', 'Düsseldorf',
        'Essen', 'Frankfurt', 'Genoa', 'Glasgow', 'Gothenburg', 'Hamburg', 'Hannover',
        'Helsinki', 'Leeds', 'Leipzig', 'Lisbon', 'Łódź', 'London', 'Kraków', 'Madrid',
        'Málaga', 'Manchester', 'Marseille', 'Milan', 'Munich', 'Naples', 'Palermo',
        'Paris', 'Poznań', 'Prague', 'Riga', 'Rome', 'Rotterdam', 'Seville', 'Sheffield',
        'Sofia', 'Stockholm', 'Stuttgart', 'The Hague', 'Turin', 'Valencia', 'Vienna',
        'Vilnius', 'Warsaw', 'Wrocław', 'Zagreb', 'Zaragoza'];

    private value:any = ['Athens', 'Amsterdam'];

    public selected(value:any):void {
        console.log('Selected value is: ', value);
    }

    public removed(value:any):void {
        console.log('Removed value is: ', value);
    }

    public refreshValue(value:any):void {
        this.value = value;
    }

    public itemsToString(value:Array<any> = []):string {
        return value
            .map((item:any) => {
                return item.text;
            }).join(',');
    }
}