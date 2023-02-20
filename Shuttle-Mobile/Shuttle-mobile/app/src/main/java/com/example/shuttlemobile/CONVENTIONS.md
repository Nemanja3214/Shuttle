Conventions.

### Id naming ###

Template: TYPE_SCOPE_DETAIL 

    TYPE
        Gui element type, helps with determining class in Java code.
        First word, short form unless it can cause ambiguity, e.g.
            RecyclerView -> recycler
            TextView -> txt
            ImageView -> img
            Button -> btn
    SCOPE
        For now, it can be either:
            p   - passenger (used in passenger activities and fragments)
            d   - driver
            u   - user (splash screen, log-in, register, anything shared like inbox etc.)

    DETAIL
        Describes the intent of the element in as few words as possible.
        For subelements inside of a container, use multiple words.

    Example: 
        A recycler view for in PassengerHistory fragment                        recycler_p_history
        Name of the passenger inside an element of the recycler view above      recycler_p_history_pname

### Adapters ###

Prefer creating anonymous classes for one-time adapters.
To reduce boilerplate, create abstract adapters.

### View for lists ###

Use ListView instead of RecyclerView because it's less code.

### Other ###

* Don't use hardcoded strings for bundle parameter names. Use `public static final String`. 
The value is defined inside the class which uses those parameters to READ from.