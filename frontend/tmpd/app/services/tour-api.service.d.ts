import { Observable } from 'rxjs';
import type { Tour } from '../models/tour.model';
export declare class TourApiService {
    private readonly http;
    private readonly resourceUrl;
    getAll(): Observable<Tour[]>;
    getById(id: number): Observable<Tour>;
    create(tour: Tour): Observable<Tour>;
}
