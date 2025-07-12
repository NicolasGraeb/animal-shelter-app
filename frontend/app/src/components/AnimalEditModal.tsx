import React, { useState, useEffect } from 'react'
import '../assets/AnimalEditModal.css'

export interface Animal {
  id: number
  name: string
  species: string
  breed: string
  age: number
  description: string
  imageData?: string
}

interface Props {
  animal: Animal | null
  visible: boolean
  onClose: () => void
  onSave: (updated: Animal) => void
}

export default function AnimalEditModal({ animal, visible, onClose, onSave }: Props) {
  const [form, setForm] = useState({
    name: '',
    species: '',
    age: 0,
    description: '',
    status: 'AVAILABLE',
  })

  useEffect(() => {
    if (animal) {
      setForm({
        name: animal.name,
        species: animal.species,
        age: animal.age,
        description: animal.description,
        status: (animal as any).status || 'AVAILABLE',
      })
    }
  }, [animal])

  const handleChange = (e: React.ChangeEvent<HTMLInputElement|HTMLTextAreaElement|HTMLSelectElement>) => {
    const { name, value } = e.target
    setForm(f => ({
      ...f,
      [name]: name === 'age' ? Number(value) : value,
    }))
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    if (animal) {
      onSave({ ...animal, ...form })
    }
  }

  if (!visible || !animal) return null

  return (
    <div className="modal-overlay">
      <div className="modal">
        <h3>Edytuj zwierzaka</h3>
        <form onSubmit={handleSubmit}>
          <div className="modal-row">
            <label>Nazwa:</label>
            <input name="name" value={form.name} onChange={handleChange} required />
          </div>
          <div className="modal-row">
            <label>Gatunek:</label>
            <input name="species" value={form.species} onChange={handleChange} required />
          </div>
          <div className="modal-row">
            <label>Wiek:</label>
            <input name="age" type="number" value={form.age} onChange={handleChange} required />
          </div>
          <div className="modal-row">
            <label>Status:</label>
            <select name="status" value={form.status} onChange={handleChange}>
              <option value="AVAILABLE">AVAILABLE</option>
              <option value="PENDING">PENDING</option>
              <option value="ADOPTED">ADOPTED</option>
            </select>
          </div>
          <div className="modal-row">
            <label>Opis:</label>
            <textarea name="description" value={form.description} onChange={handleChange} />
          </div>
          <div className="modal-actions">
            <button type="submit">Zapisz</button>
            <button type="button" onClick={onClose}>Anuluj</button>
          </div>
        </form>
      </div>
    </div>
  )
}
