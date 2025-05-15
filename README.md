# Shankproof Stats README

I'm building a Vaadin Flow app in Java called ShankProof Stats. It's a golf analytics tool that helps users track strokes gained data per round.

The key concepts are:

    Player: name, list of rounds played

    Round: date, associated player, list of per-hole stats

    HoleStat: hole number, par, and strokes gained values for four categories: OTT (Off the Tee), Approach, Short Game, Putting

I want to build:

    A form to create new players

    A form to enter a new round for a player

    A grid to display all rounds with summary stats (like total strokes gained)

    A detail view for a round showing all 18 holes and their SG values

I'm using Vaadin Flow with Java, and I want to structure the code using Spring Boot best practices. Please help me scaffold the domain model, basic views, and layout the navigation structure.

## Running the Application

To start the application in development mode, import it into your IDE and run the `Application` class. 
You can also start the application from the command line by running: 

```bash
./mvnw
```

To build the application in production mode, run:

```bash
./mvnw -Pproduction package
```

## Getting Started

The [Getting Started](https://vaadin.com/docs/latest/getting-started) guide will quickly familiarize you with your new
Shankproof Stats implementation. You'll learn how to set up your development environment, understand the project 
structure, and find resources to help you add muscles to your skeleton — transforming it into a fully-featured 
application.
