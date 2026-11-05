
import { useEffect, useState } from 'react'
import api from './api'

export default function App() {
  const [token, setToken] = useState(localStorage.getItem('peoplehub_token'))
  const [user, setUser] = useState(localStorage.getItem('peoplehub_user') || '')
  const [role, setRole] = useState(localStorage.getItem('peoplehub_role') || '')
  const [username, setUsername] = useState('admin')
  const [password, setPassword] = useState('')
  const [section, setSection] = useState('overview')
  const [message, setMessage] = useState('')
  const [data, setData] = useState([])
  const [departments, setDepartments] = useState([])
  const [employeeForm, setEmployeeForm] = useState({
    username: '',
    email: '',
    temporaryPassword: 'Welcome123!',
    fullName: '',
    designation: '',
    phone: '',
    joiningDate: '',
    departmentId: ''
  })
  const [departmentName, setDepartmentName] = useState('')
  const [editingEmployee, setEditingEmployee] = useState(null)
  const [editForm, setEditForm] = useState({
    fullName: '', designation: '', phone: '', joiningDate: '', departmentId: ''
  })
  const [payrollForm, setPayrollForm] = useState({
    employeeId: '', payMonth: '', basic: '', allowances: '0', deductions: '0', paidOn: ''
  })
  const [employeeOptions, setEmployeeOptions] = useState([])

  async function login(e) {
    e.preventDefault()
    try {
      const response = await api.post('/auth/login', { username, password })
      const loginData = response.data

      localStorage.setItem('peoplehub_token', loginData.token)
      localStorage.setItem('peoplehub_user', loginData.username)
      localStorage.setItem('peoplehub_role', loginData.role)

      setToken(loginData.token)
      setUser(loginData.username)
      setRole(loginData.role)
      setMessage('Welcome to PeopleHub')
    } catch {
      setMessage('Login failed. Check your username, password and backend connection.')
    }
  }

  function logout() {
    localStorage.removeItem('peoplehub_token')
    localStorage.removeItem('peoplehub_user')
    localStorage.removeItem('peoplehub_role')
    setToken(null)
    setUser('')
    setRole('')
    setData([])
    setDepartments([])
    setMessage('You have signed out.')
  }

  async function load() {
    try {
      if (section === 'overview') {
        const [departmentResponse, employeeResponse] = await Promise.all([
          api.get('/departments'),
          role === 'ADMIN' ? api.get('/employees') : api.get('/employees/me')
        ])

        setDepartments(departmentResponse.data)
        setData(
          Array.isArray(employeeResponse.data)
            ? employeeResponse.data
            : employeeResponse.data
              ? [employeeResponse.data]
              : []
        )
      } else {
        const endpoint = {
          employees: '/employees',
          departments: '/departments',
          attendance: role === 'ADMIN' ? '/attendance' : '/attendance/me',
          leaves: '/leaves',
          payroll: '/payroll'
        }[section]

        const response = await api.get(endpoint)
        setData(
          Array.isArray(response.data)
            ? response.data
            : response.data
              ? [response.data]
              : []
        )

        if (section === 'departments') {
          setDepartments(Array.isArray(response.data) ? response.data : [])
        }

        if (section === 'payroll' && role === 'ADMIN') {
          const employeeResponse = await api.get('/employees')
          setEmployeeOptions(Array.isArray(employeeResponse.data) ? employeeResponse.data : [])
        }
      }
    } catch (err) {
      setData([])
      setMessage(
        err.response?.data?.message ||
          'Could not load this section. Check permissions or complete your employee profile.'
      )
    }
  }

  useEffect(() => {
    if (token) {
      load()
    }
  }, [section, token])

  async function addDepartment(e) {
    e.preventDefault()
    try {
      await api.post('/departments', {
        name: departmentName,
        description: ''
      })
      setDepartmentName('')
      setMessage('Department added successfully.')
      await load()
    } catch (err) {
      setMessage(err.response?.data?.message || 'Could not add department.')
    }
  }

  async function deleteDepartment(id) {
    const confirmed = window.confirm(
      'Are you sure you want to delete this department?'
    )

    if (!confirmed) {
      return
    }

    try {
      await api.delete(`/departments/${id}`)
      setMessage('Department deleted successfully.')
      await load()
    } catch (err) {
      setMessage(
        err.response?.data?.message ||
          'Could not delete department. It may be assigned to employees.'
      )
    }
  }

  async function addEmployee(e) {
    e.preventDefault()
    try {
      await api.post('/employees', {
        ...employeeForm,
        departmentId: employeeForm.departmentId
          ? Number(employeeForm.departmentId)
          : null,
        joiningDate: employeeForm.joiningDate || null
      })

      setMessage(
        'Employee account created. Share the temporary password securely for this demo.'
      )
      setEmployeeForm({
        username: '',
        email: '',
        temporaryPassword: 'Welcome123!',
        fullName: '',
        designation: '',
        phone: '',
        joiningDate: '',
        departmentId: ''
      })
      await load()
   } catch (err) {
  console.log('Employee creation error:', err.response || err);

  setMessage(
    err.response?.data?.message ||
    `Error ${err.response?.status || ''}: Could not create employee.`
  );
}
  }

  function startEditEmployee(employee) {
    setEditingEmployee(employee)
    setEditForm({
      fullName: employee.fullName || '',
      designation: employee.designation || '',
      phone: employee.phone || '',
      joiningDate: employee.joiningDate || '',
      departmentId: employee.department?.id ? String(employee.department.id) : ''
    })
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  async function saveEmployee(e) {
    e.preventDefault()
    try {
      await api.put(`/employees/${editingEmployee.id}`, {
        ...editForm,
        departmentId: editForm.departmentId ? Number(editForm.departmentId) : null,
        joiningDate: editForm.joiningDate || null
      })
      setEditingEmployee(null)
      setMessage('Employee details updated successfully.')
      await load()
    } catch (err) {
      setMessage(err.response?.data?.message || 'Could not update employee.')
    }
  }

  async function deleteEmployee(id) {
    if (!window.confirm('Delete this employee? Their login will be deactivated, but attendance, leave and payroll history will be retained.')) {
      return
    }
    try {
      await api.delete(`/employees/${id}`)
      setMessage('Employee deactivated successfully.')
      await load()
    } catch (err) {
      setMessage(err.response?.data?.message || 'Could not delete employee.')
    }
  }

  async function addPayroll(e) {
    e.preventDefault()
    try {
      await api.post('/payroll', {
        employeeId: Number(payrollForm.employeeId),
        payMonth: payrollForm.payMonth,
        basic: Number(payrollForm.basic),
        allowances: Number(payrollForm.allowances || 0),
        deductions: Number(payrollForm.deductions || 0),
        paidOn: payrollForm.paidOn || null
      })
      setPayrollForm({
        employeeId: '', payMonth: '', basic: '', allowances: '0', deductions: '0', paidOn: ''
      })
      setMessage('Payroll record added successfully.')
      await load()
    } catch (err) {
      setMessage(err.response?.data?.message || 'Could not add payroll record.')
    }
  }

  async function action(path, body = {}) {
    try {
      await api.post(path, body)
      setMessage('Action completed.')
      await load()
    } catch (err) {
      setMessage(err.response?.data?.message || 'Action failed.')
    }
  }

  async function applyLeave(e) {
    e.preventDefault()
    const formData = new FormData(e.currentTarget)

    try {
      await api.post('/leaves', {
        startDate: formData.get('startDate'),
        endDate: formData.get('endDate'),
        reason: formData.get('reason')
      })
      e.currentTarget.reset()
      setMessage('Leave request submitted.')
      await load()
    } catch (err) {
      setMessage(err.response?.data?.message || 'leave request submitted.')
    }
  }

  if (!token) {
    return (
      <main className="login-wrap">
        <form className="login-card" onSubmit={login}>
          <div className="brand-mark">P</div>
          <p className="eyebrow">PEOPLE OPERATIONS</p>
          <h1>Welcome to PeopleHub</h1>
          <p className="muted">Sign in to manage your workplace.</p>

          <label>
            Username
            <input
              value={username}
              onChange={e => setUsername(e.target.value)}
              required
            />
          </label>

          <label>
            Password
            <input
              type="password"
              value={password}
              onChange={e => setPassword(e.target.value)}
              required
            />
          </label>

          <button className="primary" type="submit">Sign in</button>

          {message && <p className="notice">{message}</p>}
          <small>Demo admin credentials are configured in the backend environment.</small>
        </form>
      </main>
    )
  }

  const nav =
    role === 'ADMIN'
      ? ['overview', 'employees', 'departments', 'attendance', 'leaves', 'payroll']
      : ['overview', 'attendance', 'leaves', 'payroll']

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand">
          <span className="brand-mark small">P</span>
          <b>PeopleHub</b>
        </div>

        <p className="side-label">WORKSPACE</p>

        {nav.map(item => (
          <button
            key={item}
            className={section === item ? 'nav active' : 'nav'}
            onClick={() => setSection(item)}
          >
            {item[0].toUpperCase() + item.slice(1)}
          </button>
        ))}

        <div className="side-bottom">
          <div className="avatar">{user.slice(0, 1).toUpperCase()}</div>
          <div>
            <b>{user}</b>
            <small>{role}</small>
          </div>
          <button className="logout" onClick={logout}>↗</button>
        </div>
      </aside>

      <main className="content">
        <header>
          <div>
            <p className="eyebrow">PEOPLEHUB / {section.toUpperCase()}</p>
            <h1>
              {section === 'overview'
                ? 'Dashboard'
                : section[0].toUpperCase() + section.slice(1)}
            </h1>
          </div>
          <span className="role-pill">{role}</span>
        </header>

        {message && (
          <div className="notice">
            {message}
            <button onClick={() => setMessage('')}>×</button>
          </div>
        )}

        {section === 'overview' && (
          <>
            <div className="welcome">
              <div>
                <p className="eyebrow light">YOUR WORKSPACE</p>
                <h2>Good to see you, {user}.</h2>
                <p>Manage people, attendance and everyday HR activities in one place.</p>
              </div>
              <div className="welcome-icon">✦</div>
            </div>

            <div className="stats">
              <div className="stat">
                <span>Departments</span>
                <strong>{departments.length}</strong>
              </div>
              <div className="stat">
                <span>{role === 'ADMIN' ? 'Employees' : 'Your profile'}</span>
                <strong>{data.length}</strong>
              </div>
              <div className="stat">
                <span>Account</span>
                <strong className="status">Active</strong>
              </div>
            </div>

            <h3>Quick actions</h3>
            <div className="quick">
              {role === 'EMPLOYEE' ? (
                <>
                  <button onClick={() => action('/attendance/check-in')}>Check in</button>
                  <button onClick={() => action('/attendance/check-out')}>Check out</button>
                  <button onClick={() => setSection('leaves')}>Apply for leave</button>
                </>
              ) : (
                <>
                  <button onClick={() => setSection('employees')}>Manage employees</button>
                  <button onClick={() => setSection('departments')}>Manage departments</button>
                  <button onClick={() => setSection('leaves')}>Review leave requests</button>
                </>
              )}
            </div>
          </>
        )}

        {section === 'employees' && (
          <>
            {role === 'ADMIN' && (
              <form className="panel form-grid" onSubmit={addEmployee}>
                <h3 className="full">Create employee</h3>

                {[
                  ['fullName', 'Full name'],
                  ['username', 'Username'],
                  ['email', 'Email'],
                  ['temporaryPassword', 'Temporary password'],
                  ['designation', 'Designation'],
                  ['phone', 'Phone'],
                  ['joiningDate', 'Joining date']
                ].map(([field, label]) => (
                  <label key={field}>
                    {label}
                    <input
                      type={
                        field === 'joiningDate'
                          ? 'date'
                          : field === 'email'
                            ? 'email'
                            : 'text'
                      }
                      required={['fullName', 'username', 'email', 'temporaryPassword'].includes(field)}
                      value={employeeForm[field]}
                      onChange={e =>
                        setEmployeeForm({
                          ...employeeForm,
                          [field]: e.target.value
                        })
                      }
                    />
                  </label>
                ))}

                <label>
                  Department
                  <select
                    value={employeeForm.departmentId}
                    onChange={e =>
                      setEmployeeForm({
                        ...employeeForm,
                        departmentId: e.target.value
                      })
                    }
                  >
                    <option value="">No department</option>
                    {departments.map(department => (
                      <option value={department.id} key={department.id}>
                        {department.name}
                      </option>
                    ))}
                  </select>
                </label>

                <button className="primary full" type="submit">
                  Create employee
                </button>
              </form>
            )}

            {editingEmployee && (
              <form className="panel form-grid" onSubmit={saveEmployee}>
                <h3 className="full">Edit employee: {editingEmployee.fullName}</h3>
                <label>Full name<input value={editForm.fullName} required onChange={e => setEditForm({ ...editForm, fullName: e.target.value })} /></label>
                <label>Designation<input value={editForm.designation} onChange={e => setEditForm({ ...editForm, designation: e.target.value })} /></label>
                <label>Phone<input value={editForm.phone} onChange={e => setEditForm({ ...editForm, phone: e.target.value })} /></label>
                <label>Joining date<input type="date" value={editForm.joiningDate} onChange={e => setEditForm({ ...editForm, joiningDate: e.target.value })} /></label>
                <label>Department
                  <select value={editForm.departmentId} onChange={e => setEditForm({ ...editForm, departmentId: e.target.value })}>
                    <option value="">No department</option>
                    {departments.map(department => <option key={department.id} value={department.id}>{department.name}</option>)}
                  </select>
                </label>
                <button className="primary" type="submit">Save changes</button>
                <button type="button" onClick={() => setEditingEmployee(null)}>Cancel</button>
              </form>
            )}

            <DataTable
              data={data}
              type="employees"
              admin={role === 'ADMIN'}
              onEdit={startEditEmployee}
              onDelete={deleteEmployee}
            />
          </>
        )}

        {section === 'departments' && (
          <>
            {role === 'ADMIN' && (
              <form className="inline-form panel" onSubmit={addDepartment}>
                <input
                  placeholder="Department name"
                  value={departmentName}
                  onChange={e => setDepartmentName(e.target.value)}
                  required
                />
                <button className="primary" type="submit">Add department</button>
              </form>
            )}

            <DataTable
              data={data}
              type="departments"
              admin={role === 'ADMIN'}
              onDelete={deleteDepartment}
            />
          </>
        )}

        {section === 'attendance' && (
          <>
            <div className="quick">
              <button onClick={() => action('/attendance/check-in')}>Check in</button>
              <button onClick={() => action('/attendance/check-out')}>Check out</button>
            </div>
            <DataTable data={data} type="attendance" />
          </>
        )}

        {section === 'leaves' && (
          <>
            {role === 'EMPLOYEE' && (
              <form className="panel form-grid" onSubmit={applyLeave}>
                <h3 className="full">Apply for leave</h3>
                <label>
                  Start date
                  <input type="date" name="startDate" required />
                </label>
                <label>
                  End date
                  <input type="date" name="endDate" required />
                </label>
                <label className="full">
                  Reason
                  <textarea name="reason" required />
                </label>
                <button className="primary full" type="submit">Submit request</button>
              </form>
            )}

            <DataTable
              data={data}
              type="leaves"
              admin={role === 'ADMIN'}
              refresh={load}
              setMessage={setMessage}
            />
          </>
        )}

        {section === 'payroll' && (
          <>
            {role === 'ADMIN' && (
              <form className="panel form-grid" onSubmit={addPayroll}>
                <h3 className="full">Add employee payroll</h3>
                <label>Employee
                  <select required value={payrollForm.employeeId} onChange={e => setPayrollForm({ ...payrollForm, employeeId: e.target.value })}>
                    <option value="">Select employee</option>
                    {employeeOptions.map(employee => (
                      <option key={employee.id} value={employee.id}>{employee.employeeCode} - {employee.fullName}</option>
                    ))}
                  </select>
                </label>
                <label>Pay month (e.g. 2026-11)
                  <input type="month" required value={payrollForm.payMonth} onChange={e => setPayrollForm({ ...payrollForm, payMonth: e.target.value })} />
                </label>
                <label>Basic salary
                  <input type="number" min="0" step="0.01" required value={payrollForm.basic} onChange={e => setPayrollForm({ ...payrollForm, basic: e.target.value })} />
                </label>
                <label>Allowances
                  <input type="number" min="0" step="0.01" value={payrollForm.allowances} onChange={e => setPayrollForm({ ...payrollForm, allowances: e.target.value })} />
                </label>
                <label>Deductions
                  <input type="number" min="0" step="0.01" value={payrollForm.deductions} onChange={e => setPayrollForm({ ...payrollForm, deductions: e.target.value })} />
                </label>
                <label>Paid on
                  <input type="date" value={payrollForm.paidOn} onChange={e => setPayrollForm({ ...payrollForm, paidOn: e.target.value })} />
                </label>
                <p className="full"><strong>Net salary: ₹{(Number(payrollForm.basic || 0) + Number(payrollForm.allowances || 0) - Number(payrollForm.deductions || 0)).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</strong></p>
                <button className="primary full" type="submit" disabled={!payrollForm.employeeId}>Save payroll</button>
              </form>
            )}
            <DataTable data={data} type="payroll" />
          </>
        )}
      </main>
    </div>
  )
}

function DataTable({ data, type, admin, refresh, setMessage, onDelete, onEdit }) {
  const columns = {
    employees: ['employeeCode', 'fullName', 'designation', 'phone', 'joiningDate'],
    departments: ['id', 'name', 'description'],
    attendance: ['workDate', 'checkIn', 'checkOut'],
    leaves: ['id', 'startDate', 'endDate', 'reason', 'status'],
    payroll: ['employeeCode', 'fullName', 'payMonth', 'basic', 'allowances', 'deductions', 'netPay', 'paidOn']
  }[type] || []

  async function decideLeave(id, status) {
    try {
      await api.patch(`/leaves/${id}/decision?status=${status}`)
      setMessage(`Leave ${status.toLowerCase()}.`)
      if (refresh) {
        await refresh()
      }
    } catch {
      setMessage(`Could not ${status.toLowerCase()} leave.`)
    }
  }

  return (
    <div className="panel table-wrap">
      <div className="table-head">
        <h3>{type[0].toUpperCase() + type.slice(1)} records</h3>
        <span>{data.length} records</span>
      </div>

      {data.length === 0 ? (
        <p className="empty">No records to display yet.</p>
      ) : (
        <table>
          <thead>
            <tr>
              {columns.map(column => (
                <th key={column}>
                  {column.replace(/([A-Z])/g, ' $1')}
                </th>
              ))}
              {admin && type === 'leaves' && <th>Decision</th>}
              {admin && type === 'departments' && <th>Actions</th>}
              {admin && type === 'employees' && <th>Actions</th>}
            </tr>
          </thead>

          <tbody>
            {data.map((record, index) => (
              <tr key={record.id || index}>
                {columns.map(column => (
                  <td key={column}>
                    {column === 'status'
                      ? <span className="status">{record[column]}</span>
                      : column === 'fullName' && type === 'payroll'
                        ? (record.employee?.fullName || '—')
                        : column === 'employeeCode' && type === 'payroll'
                          ? (record.employee?.employeeCode || '—')
                          : record[column] ?? '—'}
                  </td>
                ))}

                {admin && type === 'leaves' && (
                  <td>
                    <button
                      className="tiny"
                      onClick={() => decideLeave(record.id, 'APPROVED')}
                    >
                      Approve
                    </button>
                    <button
                      className="tiny danger"
                      onClick={() => decideLeave(record.id, 'REJECTED')}
                    >
                      Reject
                    </button>
                  </td>
                )}

                {admin && type === 'employees' && (
                  <td>
                    <button className="tiny" onClick={() => onEdit(record)}>Edit</button>
                    <button className="tiny danger" onClick={() => onDelete(record.id)}>Delete</button>
                  </td>
                )}

                {admin && type === 'departments' && (
                  <td>
                    <button
                      className="tiny danger"
                      onClick={() => onDelete(record.id)}
                    >
                      Delete
                    </button>
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}
