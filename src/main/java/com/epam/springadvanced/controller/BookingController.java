package com.epam.springadvanced.controller;

import com.epam.springadvanced.entity.Ticket;
import com.epam.springadvanced.repository.TicketRepository;
import com.epam.springadvanced.service.BookingService;
import com.epam.springadvanced.service.EventService;
import com.epam.springadvanced.service.UserService;
import com.epam.springadvanced.service.exception.EventNotAssignedException;
import com.epam.springadvanced.service.exception.TicketAlreadyBookedException;
import com.epam.springadvanced.service.exception.TicketWithoutEventException;
import com.epam.springadvanced.service.exception.UserNotRegisteredException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@RestController
public class BookingController {
    @Autowired
    BookingService bookingService;
    @Autowired
    EventService eventService;
    @Autowired
    UserService userService;
    @Autowired
    TicketRepository ticketRepository;

    @RequestMapping(value = "/booking/cost", method = RequestMethod.GET)
    public float getCost(@RequestParam long eventId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime,
                         @RequestParam long userId, @RequestParam int seat)
            throws UserNotRegisteredException, EventNotAssignedException {
        Collection<Integer> seats = new ArrayList<>();
        seats.add(seat);
        return bookingService.getTicketPrice(eventService.getById(eventId), dateTime, seats, userService.getById(userId));
    }

    @RequestMapping(value = "/booking/book", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void bookTicket(@RequestParam long userId, @RequestParam long ticketId)
            throws UserNotRegisteredException, TicketAlreadyBookedException, TicketWithoutEventException {
        bookingService.bookTicket(userService.getById(userId), ticketRepository.getById(ticketId));
    }

    // http://localhost:8080/booking/tickets?eventId=1&dateTime=2016-04-11T18:00
    @RequestMapping(value = "/booking/tickets", method = RequestMethod.GET)
    public Collection<Ticket>  getTickets(@RequestParam long eventId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime){
        return bookingService.getTicketsForEvent(eventService.getById(eventId), dateTime);
    }
}
