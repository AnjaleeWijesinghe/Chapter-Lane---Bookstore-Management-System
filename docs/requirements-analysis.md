# Requirements Analysis

## Source Documents Reviewed

- `SE1020 - Project Description.pdf`
- `Marking Guide.pdf`
- `SE1020 - Sample Project Workload Distribution.pdf`

## What Is Mandatory

- Build a **web-based Java application**
- Use **IntelliJ IDEA**
- Use **OOP concepts**
- Implement CRUD-oriented functionality
- Provide a **user-friendly interface**
- Show strong **backend Java logic**
- Include documentation and sample data

## Important Clarification

The documents about the **Online Library Management System** are **sample documents only**. They define workload style and possible module structure, but the actual topic for this project is:

**Online Bookstore Management System**

That means the final implementation must use bookstore language, bookstore data, and bookstore workflows.

## Marking Guide Translation

- Functionality of CRUD operations:
  - Every module includes practical management actions.
- OOP concepts:
  - Abstract base classes, inheritance, polymorphic display logic, encapsulation.
- File handling:
  - All main records are persisted to `.txt` files.
- UI design:
  - Responsive visual design, custom styling, and page animations.
- Documentation:
  - README and class diagram included.

## Final Bookstore Module Decisions

- User Management:
  - Register, login, update profile, search/list, delete user
- Book Management:
  - Add, read/filter, edit, delete books
- Cart Management:
  - Add to cart, update quantity, remove item, clear cart
- Order Management:
  - Place order, view orders, update status, cancel/delete
- Admin Management:
  - Create admin, list admins, update admin, delete admin, audit log

## Design Decision

The project uses **file-based persistence** instead of SQL because that is the safest match to the assignment brief and the explicit file-handling mark allocation.
