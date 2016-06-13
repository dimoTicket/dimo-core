export class InMemoryDataService {
    createDb() {
        let tickets = [
            {"id": 1, "message": "Mr. Nice", "status": "New", "datetime": "2016-04-17 13:10:55"},
            {"id": 2, "message": "Narco", "status": "New", "datetime": "2016-04-17 13:10:55"},
            {"id": 3, "message": "Bombasto", "status": "New", "datetime": "2016-04-17 13:10:55"},
            {"id": 4, "message": "Celeritas", "status": "New", "datetime": "2016-04-17 13:10:55"},
            {"id": 5, "message": "Magneta", "status": "New", "datetime": "2016-04-17 13:10:55"},
            {"id": 6, "message": "RubberMan", "status": "New", "datetime": "2016-04-17 13:10:55"},
            {"id": 7, "message": "Dynama", "status": "New", "datetime": "2016-04-17 13:10:55"},
            {"id": 8, "message": "Dr IQ", "status": "New", "datetime": "2016-04-17 13:10:55"},
            {"id": 9, "message": "Magma", "status": "New", "datetime": "2016-04-17 13:10:55"},
            {"id": 10, "message": "Tornado", "status": "Assigned", "datetime": "2016-04-17 13:10:55"}
        ];
        return {tickets};
    }
}
