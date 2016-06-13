export class InMemoryDataService {
    createDb() {
        let tickets = [
            {"id": 1, "message": "Mr. Nice", "status": "New"},
            {"id": 2, "message": "Narco", "status": "New"},
            {"id": 3, "message": "Bombasto", "status": "New"},
            {"id": 4, "message": "Celeritas", "status": "New"},
            {"id": 5, "message": "Magneta", "status": "New"},
            {"id": 6, "message": "RubberMan", "status": "New"},
            {"id": 7, "message": "Dynama", "status": "New"},
            {"id": 8, "message": "Dr IQ", "status": "New"},
            {"id": 9, "message": "Magma", "status": "New"},
            {"id": 10, "message": "Tornado", "status": "Assigned"}
        ];
        return {tickets};
    }
}
