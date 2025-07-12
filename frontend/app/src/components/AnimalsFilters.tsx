import { useState } from 'react';

export default function AnimalsFilters() {

    const[filters, setFilters] = useState({
        species: '',
        age: '',
        habit: ''
    })

    const handleFilterChange =

    return(
        <div className="filters">
        <form>
            <input type="text" />
            <input type="number" />
            <input type="text" />

                <button type="submit">Znajdź swojego zwierzaka</button>
        </form>
        </div>
    )
}