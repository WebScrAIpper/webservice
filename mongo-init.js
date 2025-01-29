db.createUser(
    {
        user: "polytech",
        pwd: "polytech",
        roles: [
            {
                role: "readWrite",
                db: "database"
            }
        ]
    }
);