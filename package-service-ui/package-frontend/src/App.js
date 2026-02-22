import React, { useState } from "react";
import "./App.css";
import PackageList from "./components/PackageList";
import CreatePackageForm from "./components/CreatePackageForm";

function App() {
  const [refresh, setRefresh] = useState(false);

  function handleCreated() {
    setRefresh(!refresh);
  }

  return (
    <div className="app-container">
      <h1>Package Service</h1>

      <div className="card">
        <CreatePackageForm onCreated={handleCreated} />
      </div>

      <div className="card">
        <PackageList key={refresh} />
      </div>
    </div>
  );
}

export default App;