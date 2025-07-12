import React, { useEffect, useState, FormEvent, ChangeEvent } from 'react'
import { useApi } from '../hooks/useApi'
import '../assets/ProfilePage.css'

interface UserProfile {
  id: number
  username: string
  email: string
  role: string
  createdAt: string
}

interface Adoption {
  id: number
  animalId?: number
  animalName?: string
  animalBreed?: string
  animalAge?: number
  requestDate: string
  status: string
  decisionDate?: string
}

export default function ProfilePage() {
  const api = useApi()
  const [profile, setProfile] = useState<UserProfile | null>(null)
  const [tab, setTab] = useState<'adoptions' | 'requests' | 'manage' | 'add'>('adoptions')
  const [adoptions, setAdoptions] = useState<Adoption[]>([])
  const [requests, setRequests] = useState<Adoption[]>([])
  const [allRequests, setAllRequests] = useState<Adoption[]>([])
  const [msg, setMsg] = useState<string>('')
  const [imageFile, setImageFile] = useState<File | null>(null)
  const [formFields, setFormFields] = useState({
    name: '',
    species: '',
    sex: 'MALE' as 'MALE' | 'FEMALE',
    age: 0,
    description: '',
    status: 'AVAILABLE' as 'AVAILABLE' | 'PENDING' | 'ADOPTED',
  })

  useEffect(() => {
    api.get('/users/me')
      .then(u => {
        console.log('PROFILE →', u)
        setProfile(u)
      })
      .catch(console.error)
    api.get('/users/me/adoptions')
      .then(a => {
        console.log('MY ADOPTIONS →', a)
        setAdoptions(a)
      })
      .catch(console.error)
    api.get('/users/me/requests')
      .then(r => {
        console.log('MY REQUESTS →', r)
        setRequests(r)
      })
      .catch(console.error)
  }, [])

  useEffect(() => {
    if (tab === 'manage') {
      api.get('/adoptions')
        .then(all => {
          console.log('ALL REQUESTS →', all)
          setAllRequests(all)
        })
        .catch(console.error)
    }
  }, [tab])

  if (!profile) return <div>Ładowanie profilu...</div>
  const isAdmin = profile.role === 'ADMIN'

  const approve = async (id: number) => {
    try {
      await api.patch(`/adoptions/${id}/status`, { status: 'APPROVED' })
      setAllRequests(allRequests.filter(a => a.id !== id))
    } catch {
      alert('Błąd przy akceptacji wniosku')
    }
  }

  const reject = async (id: number) => {
    try {
      await api.patch(`/adoptions/${id}/status`, { status: 'REJECTED' })
      setAllRequests(allRequests.filter(a => a.id !== id))
    } catch {
      alert('Błąd przy odrzuceniu wniosku')
    }
  }

  const handleImageChange = (e: ChangeEvent<HTMLInputElement>) => {
    setImageFile(e.target.files?.[0] ?? null)
  }

  const handleAddAnimal = async (e: FormEvent) => {
    e.preventDefault()
    setMsg('')

    const data = new FormData()
    data.append('name', formFields.name)
    data.append('species', formFields.species)
    data.append('sex', formFields.sex)
    data.append('age', String(formFields.age))
    data.append('description', formFields.description)
    data.append('status', formFields.status)
    if (imageFile) data.append('image', imageFile)

    try {
      await api.post('/animals', data)
      setMsg('Nowe zwierzę dodane!')
      setFormFields({ name: '', species: '', sex: 'MALE', age: 0, description: '', status: 'AVAILABLE' })
      setImageFile(null)
    } catch (err: any) {
      setMsg('Błąd: ' + err.message)
    }
  }

  return (
    <div className="profile-page">
      <h2>Profil użytkownika</h2>
      <p><strong>ID:</strong> {profile.id}</p>
      <p><strong>Login:</strong> {profile.username}</p>
      <p><strong>Email:</strong> {profile.email}</p>
      <p><strong>Rola:</strong> {profile.role}</p>
      <p><strong>Zarejestrowany:</strong> {new Date(profile.createdAt).toLocaleDateString()}</p>

      <div className="tabs">
        <button className={tab==='adoptions' ? 'active':''} onClick={()=>setTab('adoptions')}>Moje adopcje</button>
        <button className={tab==='requests'  ? 'active':''} onClick={()=>setTab('requests')}>Wnioski</button>
        {isAdmin && <>
          <button className={tab==='manage' ? 'active':''} onClick={()=>setTab('manage')}>Zarządzaj wnioskami</button>
          <button className={tab==='add'    ? 'active':''} onClick={()=>setTab('add')}>Dodaj zwierzę</button>
        </>}
      </div>

      {tab === 'adoptions' && (
        <>
          <h3>Moje zatwierdzone adopcje</h3>
          {adoptions.length ? (
            <ul className="list">
              {adoptions.map(a => (
                <li key={a.id}>
                  <strong>{a.animalName || '—'}</strong>, {a.animalAge ?? '—'} lat<br/>
                  <small>Decyzja: {a.decisionDate
                    ? new Date(a.decisionDate).toLocaleDateString()
                    : '—'}</small>
                </li>
              ))}
            </ul>
          ) : <p>Brak zatwierdzonych adopcji.</p>}
        </>
      )}

      {tab === 'requests' && (
        <>
          <h3>Moje wnioski</h3>
          {requests.length ? (
            <ul className="list">
              {requests.map(a => (
                <li key={a.id}>
                  {a.animal?.name || '—'} — {a.status} ({new Date(a.requestDate).toLocaleDateString()})
                </li>
              ))}
            </ul>
          ) : <p>Nie wysłano żadnych wniosków.</p>}
        </>
      )}

      {isAdmin && tab === 'manage' && (
        <>
          <h3>Zarządzaj wnioskami (tylko oczekujące)</h3>
          {allRequests.filter(a => a.status === 'PENDING').length ? (
            <table className="manage-table">
              <thead>
                <tr>
                  <th>ID</th><th>Zwierzę</th><th>Data</th><th>Status</th><th>Akcje</th>
                </tr>
              </thead>
              <tbody>
                {allRequests
                  .filter(a => a.status === 'PENDING')
                  .map(a => (
                    <tr key={a.id}>
                      <td>{a.id}</td>
                      <td>{a.animalName || '—'}</td>
                      <td>{new Date(a.requestDate).toLocaleDateString()}</td>
                      <td>{a.status}</td>
                      <td>
                        <button onClick={()=>approve(a.id)}>Akceptuj</button>
                        <button onClick={()=>reject(a.id)}>Odrzuć</button>
                      </td>
                    </tr>
                ))}
              </tbody>
            </table>
          ) : <p>Brak oczekujących wniosków do zarządzania.</p>}
        </>
      )}

      {isAdmin && tab === 'add' && (
        <>
          <h3>Dodaj zwierzę</h3>
          <form onSubmit={handleAddAnimal} className="add-animal-form" encType="multipart/form-data">
            <div>
              <label>Nazwa:</label>
              <input
                type="text"
                value={formFields.name}
                onChange={e=>setFormFields(f=>({...f, name:e.target.value}))}
                required
              />
            </div>
            <div>
              <label>Gatunek:</label>
              <input
                type="text"
                value={formFields.species}
                onChange={e=>setFormFields(f=>({...f, species:e.target.value}))}
                required
              />
            </div>
            <div>
              <label>Płeć:</label>
              <select
                value={formFields.sex}
                onChange={e=>setFormFields(f=>({...f, sex:e.target.value as 'MALE'|'FEMALE'}))}>
                <option value="MALE">MALE</option>
                <option value="FEMALE">FEMALE</option>
              </select>
            </div>
            <div>
              <label>Wiek:</label>
              <input
                type="number"
                value={formFields.age}
                onChange={e=>setFormFields(f=>({...f, age:+e.target.value}))}
                required
              />
            </div>
            <div>
              <label>Opis:</label>
              <textarea
                value={formFields.description}
                onChange={e=>setFormFields(f=>({...f, description:e.target.value}))}
              />
            </div>
            <div>
              <label>Status:</label>
              <select
                value={formFields.status}
                onChange={e=>setFormFields(f=>({...f, status:e.target.value as 'AVAILABLE'|'PENDING'|'ADOPTED'}))}>
                <option value="AVAILABLE">AVAILABLE</option>
                <option value="PENDING">PENDING</option>
                <option value="ADOPTED">ADOPTED</option>
              </select>
            </div>
            <div>
              <label>Zdjęcie:</label>
              <input type="file" accept="image/*" onChange={handleImageChange} />
            </div>
            <button type="submit">Dodaj</button>
          </form>
          {msg && <p className="message">{msg}</p>}
        </>
      )}
    </div>
  )
}
