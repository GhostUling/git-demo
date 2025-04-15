erDiagram
    PLAYER {
        int player_id PK
        varchar username
        varchar password
        varchar email
        datetime registration_time
    }
    GAME {
        int game_id PK
        varchar game_name
        varchar game_type
        decimal price
        text description
        varchar install_package_path
    }
    TRANSACTION {
        int transaction_id PK
        int player_id FK
        int game_id FK
        decimal amount
        datetime transaction_time
        enum payment_status {'paid', 'unpaid', 'cancelled'}
    }
    DEVELOPER {
        int developer_id PK
        varchar developer_name
        varchar contact_email
    }
    FRIENDSHIP {
        int friendship_id PK
        int player1_id FK
        int player2_id FK
        enum status {'pending', 'accepted', 'declined'}
        datetime friendship_start
    }
    PLAYER ||--o{ TRANSACTION : "makes"
    GAME ||--o{ TRANSACTION : "is part of"
    DEVELOPER ||--o{ GAME : "develops"
    PLAYER ||--o{ FRIENDSHIP : "participates in"
    PLAYER ||--o{ FRIENDSHIP : "participates in"
    PLAYER ||--o{ GAME : "owns"