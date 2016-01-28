package app.controllers;

import app.entities.Ticket;
import app.repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping ( "/" )
public class TicketController
{

    @Autowired
    private TicketRepository ticketRepository;

    @RequestMapping ( value = "/{id}", method = RequestMethod.GET )
    public String getTicketMessageById ( @PathVariable ( "id" ) Long id, Model model )
    {
        Ticket ticket = this.ticketRepository.findOne( id );
        if ( ticket != null )
        {
            model.addAttribute( "ticket", ticket );
            return "ticket";
        }
        return "error";
    }
}

