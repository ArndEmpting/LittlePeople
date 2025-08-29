# Complete Event-Driven Processing Flow

## Overview

The LittlePeople simulation now uses a fully event-driven architecture where all state changes happen through events. Here's how the complete processing flow works:

## Processing Flow Diagram

```
TimeChangeEvent ? DefaultMortalityProcessor ? PersonDeathEvent ? PersonDeathEventProcessor ? Person State Change
```

## Step-by-Step Flow

### 1. Time Change Event Creation
- **Source**: Simulation Clock or Time Management System
- **Event**: `TimeChangeEvent`
- **Contains**: New date, previous date, list of affected person IDs
- **Purpose**: Triggers time-based processing (aging, mortality, etc.)

### 2. Mortality Processing
- **Processor**: `DefaultMortalityProcessor`
- **Receives**: `TimeChangeEvent` with person IDs
- **Process**:
  1. Iterates through each person ID in the event
  2. Retrieves person from registry
  3. Calculates death probability based on age/health
  4. If person should die: determines cause and creates `PersonDeathEvent`
  5. Schedules the death event through `EventScheduler`

### 3. Death Event Processing
- **Processor**: `PersonDeathEventProcessor`
- **Receives**: `PersonDeathEvent`
- **Process**:
  1. Retrieves person from registry using person ID
  2. Validates person exists and is alive
  3. Performs actual state changes:
     - Sets death date
     - Sets death cause
     - Dissolves partnerships
     - Updates family relationships
  4. Marks event as processed

### 4. State Changes
- **Target**: Person object
- **Changes**: Death date, death cause, relationship updates
- **Audit**: Complete event trail of all changes

## Key Benefits

### Consistency
- All changes go through the same event system
- No direct state mutations outside of event processors
- Uniform approach for all future expansions

### Auditability
- Every change creates an event record
- Complete history of what happened when
- Easy debugging and analysis

### Schedulability
- Events can be scheduled for future execution
- Death events can be planned in advance
- Cancellable before processing

### Testability
- Easy to mock event flows
- Isolated testing of processors
- Predictable state changes

## Implementation Details

### Event Classes Created
- `TimeChangeEvent` - triggers time-based processing
- `PersonDeathEvent` - handles person death
- `PartnershipFormedEvent` - handles marriage/partnership
- `PartnershipDissolvedEvent` - handles divorce/separation
- `ChildAddedEvent` - handles parent-child relationships
- `HealthChangedEvent` - handles health status changes
- `WealthChangedEvent` - handles wealth status changes

### Event Processors Created
- `PersonDeathEventProcessor` - processes death events
- `RelationshipEventProcessor` - processes relationship events
- `EntityEventProcessor` - processes health/wealth changes

### Event Manager
- `DefaultEventDrivenPersonManager` - schedules all person-related events
- `EventDrivenPersonManager` interface - ensures event-only changes

## Usage Example

```java
// Instead of direct change:
person.markDeceased(LocalDate.now(), DeathCause.DISEASE);

// Use event-driven approach:
PersonDeathEvent deathEvent = new PersonDeathEvent(
    person.getId(), 
    LocalDate.now(), 
    DeathCause.DISEASE
);
eventManager.scheduleDeathEvent(deathEvent);
```

## Migration Path

1. **Phase 1**: Event infrastructure (? Complete)
2. **Phase 2**: Convert existing direct methods to events
3. **Phase 3**: Remove direct mutation methods from Person class
4. **Phase 4**: Add validation to prevent direct changes

This architecture ensures that all future expansions (economics, politics, disasters, etc.) will follow the same consistent event-driven pattern.
